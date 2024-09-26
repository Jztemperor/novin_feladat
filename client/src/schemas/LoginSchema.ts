import { z } from 'zod';

export const LoginSchema = z.object({
  username: z.string().min(1, { message: "A felhasználónév megadása kötelező!" }),
  password: z.string().min(1, { message: "A jelszó megadása kötelező!" }),
});
