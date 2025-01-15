'use client'

import useUser from "@/hooks/useUser";
import { logoFont } from "@/utils/font";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { use, useEffect } from "react";

const NavBar = () => {
    const user = useUser();
    const router = useRouter();
    
    useEffect(() => {
        if (!user) {
            router.push("/login");
        }
    });

    return (
        <nav className="px-12 py-4 w-100 flex flex-row items-center justify-between sticky top-0 bg-white text-black">
            {/* Temporary Logo */}
            <Link href="/" className={`text-3xl text-sky-400 ${logoFont.className}`}>DayTrader Inc.</Link>
            <ul className="flex flex-row gap-x-4 font-bold">
                <li>
                    <Link href="/account">Search</Link>
                </li>
                <li>
                    <Link href="/account">My Stocks</Link>
                </li>
                <li>
                    <Link href="/account">Account</Link>
                </li>
            </ul>
        </nav>
    )
}

export default NavBar;