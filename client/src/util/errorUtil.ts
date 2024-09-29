import { toast } from "react-toastify";

export const handleApiErrors = (errors: any) => {
  if (errors.response && errors.response.data && errors.response.data.errors) {
    Object.keys(errors.response.data.errors).forEach((key) => {
      toast.error(errors.response.data.errors[key]);
    });
  } else {
    toast.error("An unexpected error occurred.");
  }
};