import { z } from 'zod';

export const RegisterSchema = z.object({
  name: z.string().min(1, { message: "A név megadása kötelező!"}),
  username: z.string().min(1, { message: "A felhasználónév megadása kötelező!" }),
  password: z.string().min(1, { message: "A jelszó megadása kötelező!" }),
  roleName: z.string().min(1, { message: "A szerepkör kiválasztása kötelező!" })
});
