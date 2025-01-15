'use client'

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function App() {
  const router = useRouter();
  const user = false;

  useEffect(() => {
    if (user) {
      router.push("/app");
    } else {
      router.push("/login");
    }
  }, [user, router]);

  return <div>Redirecting...</div>;
}
