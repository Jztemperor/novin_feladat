import { SubmitHandler, useForm } from "react-hook-form";
import { zodResolver } from '@hookform/resolvers/zod';
import { LoginSchemaType } from "../types/LoginSchemaType";
import { LoginSchema } from "../schemas/LoginSchema";
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { Link } from 'react-router-dom';
import axios from "axios";
import { useAuth } from "../context/AuthContext";

export const Login = () => {
  const navigate = useNavigate();
  const { setAuth } = useAuth();
  const { register, handleSubmit, formState: { errors, isValid }} = useForm<LoginSchemaType>({
    resolver: zodResolver(LoginSchema),
    mode: 'onChange',
  });

  const handleBack = () => {
    navigate(-1); // Go back to the previous page
  }

  const onSubmit: SubmitHandler<LoginSchemaType> = async (data) => {
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', data);
      const { token, username, roles } = response.data;

      const authorities = roles.map((role: { authority: string }) => role.authority);
      setAuth(token, username, authorities);


      toast.success("Sikeres bejelnetkezés!");
      setTimeout(() => {
        navigate('/');
      }, 500);

    }catch(error: any) {
      console.log(error);
      if (error.response && error.response.data && error.response.data.errors) {
        const errors = error.response.data.errors;

        // ha esetleg tobb error jonne szervertol (kb lehetetlen ha nincs kinyoma JS bongeszoben..)
        Object.keys(errors).forEach(key => {
          toast.error(errors[key]);
        });
      } else {
        toast.error("Sikertelen bejelentkezés!");
      }
    }
  }

  return (
    <div className="flex min-h-full flex-col justify-center px-6 py-12 lg:px-8">
  <div className="sm:mx-auto sm:w-full sm:max-w-sm">
    <h2 className="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">Bejelentkezés</h2>
  </div>

  <div className="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
    <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>

      <div>
        <label className="block text-sm font-medium leading-6 text-gray-900">Felhasználó név</label>
        <div className="mt-2">
          <input {...register("username")} id="username" name="username" type="text"  className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"/>
          {errors.username && <span className='text-red-600'>{errors.username.message}</span>}
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium leading-6 text-gray-900">Jelszó</label>
        <div className="mt-2">
          <input {...register("password")} id="password" name="password" type="password"  className="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"/>
          {errors.password && <span className='text-red-600'>{errors.password.message}</span>}
        </div>
      </div>

      <div>
        <button type="submit" className="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600 mb-3 disabled:bg-indigo-200" disabled={!isValid}>Bejelentkezés</button>
        <button type="button" className="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600" onClick={handleBack}>Vissza</button>
      </div>
    </form>

    <p className="mt-5 text-center text-sm text-gray-500">
      Nincs még fiókod?
      <Link to="/regisztracio" className="font-semibold leading-6 text-indigo-600 hover:text-indigo-500">Regisztráció</Link>
    </p>
  </div>
</div>
  )
}
