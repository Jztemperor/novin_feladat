import { RegisterSchema } from "../schemas/RegisterSchema";
import { z } from 'zod';

export type RegisterSchemaType = z.infer<typeof RegisterSchema>;