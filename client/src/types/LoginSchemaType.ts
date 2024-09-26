import { z } from 'zod';
import { LoginSchema } from '../schemas/LoginSchema';

export type LoginSchemaType = z.infer<typeof LoginSchema>;