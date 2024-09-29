import { Authority } from "./Authority";

export type User = {
  id: number;
  username: string;
  authorities: Authority[];
}