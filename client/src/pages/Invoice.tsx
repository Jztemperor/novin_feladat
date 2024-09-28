import axios from "axios";
import { useState, useEffect } from "react";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";

interface InvoiceItem {
  itemName: string;
  price: number;
}

interface InvoiceData {
  id: number;
  customerName: string;
  issueDate: string;
  dueDate: string;
  items: InvoiceItem[];
  comment: string;
  totalPrice: number;
}

export const Invoice = () => {
  const [data, setData] = useState<InvoiceData | null>();
  const navigate = useNavigate();
  const { token } = useAuth();
  const param = useParams();

  const handleBack = () => {
    navigate(-1); // Go back to the previous page
  };

  const getInvocie = async (invoiceId: any) => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/invoice/${invoiceId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response?.data) {
        setData(response?.data);
      }
    } catch (errors: any) {
      toast.error("Hiba a számla lekérésében: " + errors);
    }
  };

  useEffect(() => {
    getInvocie(param.invoiceId);
  }, []);

  return (
    <div className="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-sm">
        <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">
          Számla Megtekintése
        </h2>
      </div>

      <div className="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form className="space-y-6">
          <div>
            <label className="block text-sm font-medium leading-6 text-gray-900">
              Vásárló Név
            </label>
            <div className="mt-2">
              <input
                value={data?.customerName}
                id="customerName"
                name="customerName"
                type="text"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                readOnly
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium leading-6 text-gray-900">
              Kiállítás Dátuma
            </label>
            <div className="mt-2">
              <input
                value={data?.issueDate}
                id="issueDate"
                name="issueDate"
                type="date"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                readOnly
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium leading-6 text-gray-900">
              Esedékesség Dátuma
            </label>
            <div className="mt-2">
              <input
                value={data?.dueDate}
                id="dueDate"
                name="dueDate"
                type="date"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                readOnly
              />
            </div>
          </div>

          <div>
            <h3 className="text-sm font-medium leading-6 text-gray-900">
              Tételek
            </h3>
          </div>

          {/* Render already added items */}
          {data &&
            data.items.map((item: InvoiceItem, index: number) => (
              <div key={index} className="flex justify-center space-x-2 mt-2">
                <div className="flex-1">
                  <input
                    value={item.itemName}
                    placeholder="Tétel neve"
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    readOnly
                  />
                </div>
                <div className="flex-1">
                  <input
                    value={item.price}
                    type="number"
                    placeholder="Ár"
                    className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                    readOnly
                  />
                </div>
              </div>
            ))}

          <div>
            <label className="block text-sm font-medium leading-6 text-gray-900">
              Komment
            </label>
            <div className="mt-2">
              <input
                value={data?.comment}
                id="comment"
                name="comment"
                type="text"
                className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
                readOnly
              />
            </div>
          </div>

          <div>
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
