import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

export const NavBar = ({ children }: any) => {
  const [isOpen, setIsOpen] = useState(false);
  const { logout, token, authorities } = useAuth();

  const toggleMenu = () => {
    setIsOpen(!isOpen);
  };

  return (
    <>
      <nav className="bg-gray-800">
        <div className="mx-auto px-2 sm:px-6 lg:px-8">
          <div className="relative flex h-16 items-center justify-start">
            <div className="absolute inset-y-0 left-0 flex items-center sm:hidden">
              <button
                type="button"
                className="relative inline-flex items-center justify-center rounded-md p-2 text-gray-400 hover:bg-gray-700 hover:text-white focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white"
                aria-controls="mobile-menu"
                aria-expanded={isOpen}
                onClick={toggleMenu}
              >
                <span className="absolute -inset-0.5"></span>
                <span className="sr-only">Open main menu</span>

                <svg
                  className={`${isOpen ? "hidden" : "block"} h-6 w-6`}
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth="1.5"
                  stroke="currentColor"
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5"
                  />
                </svg>

                <svg
                  className={`${isOpen ? "block" : "hidden"} h-6 w-6`}
                  fill="none"
                  viewBox="0 0 24 24"
                  strokeWidth="1.5"
                  stroke="currentColor"
                  aria-hidden="true"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            </div>
            <div className="flex flex-1 items-center justify-center sm:items-stretch sm:justify-start">
              <div className="flex flex-shrink-0 items-center">
                <img
                  className="h-8 w-auto"
                  src="https://tailwindui.com/img/logos/mark.svg?color=indigo&shade=500"
                  alt="Your Company"
                />
              </div>
              <div className="hidden sm:ml-6 sm:block">
                <div className="flex space-x-4">
                  {token && (
                    <Link
                      to="/"
                      relative="path"
                      className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
                    >
                      Főoldal
                    </Link>
                  )}

                  {!token && (
                    <Link
                      to="/bejelentkezes"
                      relative="path"
                      className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
                    >
                      Bejelentkezés
                    </Link>
                  )}

                  {!token && (
                    <Link
                      to="/regisztracio"
                      relative="path"
                      className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
                    >
                      Regisztráció
                    </Link>
                  )}

                  {token && (
                    <Link
                      to="/szamlak"
                      relative="path"
                      className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
                    >
                      Számlák listája
                    </Link>
                  )}

                  {token &&
                    (authorities.includes("Konyvelo") ||
                      authorities.includes("Adminisztrator")) && (
                      <Link
                        to="/szamla-letrehoz"
                        relative="path"
                        className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
                      >
                        Számla létrehozás
                      </Link>
                    )}

                  {token && authorities.includes("Adminisztrator") && (
                    <Link
                      to="/admin"
                      relative="path"
                      className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
                    >
                      Adminisztráció
                    </Link>
                  )}

                  {token && (
                    <button
                      onClick={logout}
                      className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
                    >
                      Kijelentkezés
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Mobile Menu */}
        <div
          className={`sm:hidden ${isOpen ? "block" : "hidden"}`}
          id="mobile-menu"
        >
          <div className="space-y-1 px-2 pb-3 pt-2">
            {token && (
              <Link
                to="/"
                relative="path"
                className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
              >
                Főoldal
              </Link>
            )}

            {!token && (
              <Link
                to="/bejelentkezes"
                relative="path"
                className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
              >
                Bejelentkezés
              </Link>
            )}

            {!token && (
              <Link
                to="/regisztracio"
                relative="path"
                className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
              >
                Regisztráció
              </Link>
            )}

            {token && (
              <Link
                to="/szamlak"
                relative="path"
                className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
              >
                Számlák listája
              </Link>
            )}

            {token &&
              (authorities.includes("Konyvelo") ||
                authorities.includes("Adminisztrator")) && (
                <Link
                  to="/szamla-letrehoz"
                  relative="path"
                  className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
                >
                  Számla létrehozás
                </Link>
              )}

            {token && authorities.includes("Adminisztrator") && (
              <Link
                to="/admin"
                relative="path"
                className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
              >
                Adminisztráció
              </Link>
            )}

            {token && (
              <button
                onClick={logout}
                className="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white"
              >
                Kijelentkezés
              </button>
            )}
          </div>
        </div>
      </nav>
      <main className="h-full">{children}</main>
    </>
  );
};
