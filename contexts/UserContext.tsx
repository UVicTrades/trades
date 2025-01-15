'use client'

import { createContext, useState, useEffect, ReactNode } from "react";
import { UserType, UserContextType as UserContextType } from "../types";

export const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<UserType | null>(null);
    const [token, settoken] = useState<string | null>(null);

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        const storedToken = localStorage.getItem("token");
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
        if (storedToken) {
            settoken(storedToken);
        }
    }, []);

    const setUserInStorage = (user: UserType, token: string) => {
        localStorage.setItem("user", JSON.stringify(user));
        localStorage.setItem("token", token);
        setUser(user);
        settoken(token);
    };

    const logout = async () => {
        setUser(null);
        settoken(null);
        localStorage.removeItem("user");
        localStorage.removeItem("token");
    };

    return (
        <UserContext value={{ user, setUserInStorage: setUser, logout }}>
            {children}
        </UserContext>
    );
};
