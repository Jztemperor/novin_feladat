import {z} from "zod";
import { CreateInvoiceSchema } from "../schemas/CreateInvoiceSchema";

export type CreateInvoiceSchemaType = z.infer<typeof CreateInvoiceSchema>;