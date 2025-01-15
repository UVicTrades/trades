'use client'

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { UserContext } from "@/contexts/UserContext"
import { use, useContext } from "react"
import { exampleUser } from "@/data"
import { useRouter } from "next/navigation"


export function LoginForm({
  className,
  ...props
}: React.ComponentPropsWithoutRef<"form">) {
  const { setUserInStorage } = useContext(UserContext) || {};
  const router = useRouter();

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();
    try {
      if (setUserInStorage) {
        setUserInStorage(exampleUser, "exampleToken123");
        router.push("/app");
      } else {
        console.error("login failed");
      }
    } catch (error) {
      console.error("Error logging in:", error);
    }
  };

  return (
    <form className={cn("flex flex-col gap-6", className)} {...props}>
      <div className="flex flex-col items-center gap-2 text-center">
        <h1 className="text-2xl font-bold">Login to your account</h1>
        <p className="text-balance text-sm text-muted-foreground">
          Enter your email below to login to your account
        </p>
      </div>
      <div className="grid gap-6">
        <div className="grid gap-2">
          <Label htmlFor="email">Email</Label>
          <Input id="email" type="email" placeholder="m@example.com" required />
        </div>
        <div className="grid gap-2">
          <div className="flex items-center">
            <Label htmlFor="password">Password</Label>
          </div>
          <Input id="password" type="password" required />
        </div>
        <Button type="submit" className="w-full" onClick={handleClick}>
          Login
          </Button>
        {/* <a href="/" className="mx-auto text-white bg-black rounded px-8 py-2">
        Login
        </a> */}
      </div>
      <div className="text-center text-sm">
        Don&apos;t have an account?{" "}
        <a href="#" className="underline underline-offset-4">
          Sign up
        </a>
      </div>
    </form>
  )
}
