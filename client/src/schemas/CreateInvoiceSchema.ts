import { z } from "zod";

export const CreateInvoiceSchema = z.object({
  customerName: z.string().min(1, "A vásárló név megadása kötelező!"),
  issueDate: z.string().min(1, "A kiállítási dátum megadása kötelező!"),
  dueDate: z.string().min(1, "Az esedékesség dátum megadása kötelező!"),
  items: z.array(
    z.object({
      itemName: z.string().min(1, "A tétel név megadása kötelező!"),
      price: z.number().min(0, "A tétel árának megadása kötelező!")
    })
  ),
  comment: z.string().min(1, "A komment megadása kötelező!")
});