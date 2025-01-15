import { GalleryVerticalEnd } from "lucide-react"
import { LoginForm } from "@/components/LoginForm"
import { logoFont } from "@/utils/font"

export default function LoginPage() {
  return (
    <div className="grid min-h-svh lg:grid-cols-2">
      <div className="flex flex-col gap-4 p-6 md:p-10">
        <div className="flex justify-center gap-2 md:justify-start">
          <a href="#" className="flex items-center gap-2 font-medium">
            <div className="flex h-6 w-6 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <GalleryVerticalEnd className="size-4" />
            </div>
            DayTrader Inc.
          </a>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-xs">
            <LoginForm />
          </div>
        </div>
      </div>
      <div className="hidden bg-sky-400 lg:flex items-center justify-center">
        <h1 className={`m-auto text-7xl text-white ${logoFont.className}`}>DayTrader Inc.</h1>
      </div>
    </div>
  )
}
