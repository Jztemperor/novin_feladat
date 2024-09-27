import { SubmitHandler, useForm, useFieldArray } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { CreateInvoiceSchema } from "../schemas/CreateInvoiceSchema";
import { CreateInvoiceSchemaType } from "../types/CreateInvoiceSchemaType";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";

export const CreateInvoice = () => {
  // Init hooks and states
  const navigate = useNavigate();
  const [currentItem, setCurrentItem] = useState({ itemName: "", price: 0 });
  const [currentItemCount, setCurrentItemCount] = useState(0);
  const { token } = useAuth();

  // Setup hookform
  const {
    register,
    handleSubmit,
    control,
    reset,
    watch,
    formState: { errors },
  } = useForm<CreateInvoiceSchemaType>({
    resolver: zodResolver(CreateInvoiceSchema),
    mode: "onChange",
  });

  let issueDate = watch("issueDate");

  // Field array for items
  const { fields, append, remove } = useFieldArray({
    control,
    name: "items",
  });

  // Add item handler
  const handleAddItem = () => {
    // If fields are filled, append and clear the input
    if (currentItem.itemName && currentItem.price > 0) {
      append(currentItem);
      setCurrentItem({ itemName: "", price: 0 });
      setCurrentItemCount(currentItemCount + 1);
    }
  };

  // Form submit handler
  const onSubmit = async (data: CreateInvoiceSchemaType) => {
    if (data.items.length > 0) {
      try {
        // Call api
        console.log(token);
        await axios.post("http://localhost:8080/api/invoice/create", data, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        toast.success("Számla létrehozva!");
        reset();
        setCurrentItemCount(0);
      } catch (errors: any) {
        Object.keys(errors).forEach((key) => {
          toast.error(errors[key]);
        });
      }
    } else {
      toast.error("Legalább 1 tétel megadása kötelező!");
    }
  };

  const handleBack = () => {
    navigate(-1); // Go back to the previous page
  };

  return (
    <div className="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-sm">
        <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
          Számla létrehozás
        </h2>
      </div>

      <div className="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
          <div>
            <label className="block text-sm font-medium leading-6 text-gray-900">
              Vásárló Név
            </label>
            <div className="mt-2">
              <input
                {...register("customerName")}
                id="customerName"
                name="customerName"
                type="text"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              {errors.customerName && (
                <span className="text-red-600">
                  {errors.customerName.message}
                </span>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium leading-6 text-gray-900">
              Kiállítás Dátuma
            </label>
            <div className="mt-2">
              <input
                {...register("issueDate")}
                id="issueDate"
                name="issueDate"
                type="date"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              {errors.issueDate && (
                <span className="text-red-600">{errors.issueDate.message}</span>
              )}
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium leading-6 text-gray-900">
              Esedékesség Dátuma
            </label>
            <div className="mt-2">
              <input
                {...register("dueDate")}
                id="dueDate"
                name="dueDate"
                type="date"
                min={issueDate}
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              {errors.dueDate && (
                <span className="text-red-600">{errors.dueDate.message}</span>
              )}
            </div>
          </div>

          <div>
            <h3 className="text-sm font-medium leading-6 text-gray-900">
              Tételek
            </h3>
          </div>

          {/* Render already added items */}
          {currentItemCount > 0 &&
            fields.map((item, index) => (
              <div key={item.id} className="flex justify-center space-x-2 mt-2">
                <div className="flex-1">
                  <input
                    {...register(`items.${index}.itemName`)}
                    placeholder="Tétel neve"
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                  />
                </div>
                <div className="flex-1">
                  <input
                    {...register(`items.${index}.price`, {
                      valueAsNumber: true,
                    })}
                    type="number"
                    placeholder="Ár"
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                  />
                </div>
                <button
                  type="button"
                  onClick={() => remove(index)}
                  className="bg-red-600 text-white font-bold py-2 px-4 rounded"
                >
                  Eltávolítás
                </button>
              </div>
            ))}

          {currentItemCount > 0 && (
            <div className="flex justify-center space-x-2 mt-2">
              {/* Item Name */}
              <div className="flex-1">
                <input
                  value={currentItem.itemName}
                  onChange={(e) =>
                    setCurrentItem({ ...currentItem, itemName: e.target.value })
                  }
                  placeholder="Tétel neve"
                  className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                />
              </div>
              {/* Item Price */}
              <div className="flex-1">
                <input
                  value={currentItem.price}
                  onChange={(e) =>
                    setCurrentItem({
                      ...currentItem,
                      price: parseFloat(e.target.value),
                    })
                  }
                  type="number"
                  placeholder="Ár"
                  className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                />
              </div>
              <button
                type="button"
                onClick={handleAddItem}
                className="bg-indigo-600 text-white font-bold py-2 px-4 rounded"
              >
                Hozzáadás
              </button>
            </div>
          )}

          {/*Render only 1 input when itemcount is 0*/}
          {currentItemCount === 0 && (
            <div className="flex space-x-2 mt-2">
              {/* Item Name */}
              <input
                value={currentItem.itemName}
                onChange={(e) =>
                  setCurrentItem({ ...currentItem, itemName: e.target.value })
                }
                placeholder="Tétel neve"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              {/* Item Price */}
              <input
                value={currentItem.price}
                onChange={(e) =>
                  setCurrentItem({
                    ...currentItem,
                    price: parseFloat(e.target.value),
                  })
                }
                type="number"
                placeholder="Ár"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              <button
                type="button"
                onClick={handleAddItem}
                className="bg-indigo-600 text-white font-bold py-2 px-4 rounded"
              >
                Hozzáadás
              </button>
            </div>
          )}

          <div>
            <label className="block text-sm font-medium leading-6 text-gray-900">
              Komment
            </label>
            <div className="mt-2">
              <input
                {...register("comment")}
                id="comment"
                name="comment"
                type="text"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
              />
              {errors.comment && (
                <span className="text-red-600">{errors.comment.message}</span>
              )}
            </div>
          </div>

          <div>
            <button
              type="submit"
              className="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 mb-3 disabled:bg-indigo-200"
            >
              Mentés
            </button>
            <button
              type="button"
              className="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
              onClick={handleBack}
            >
              Vissza
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
