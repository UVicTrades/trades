import { SignUpForm } from "@/components/SignUpForm"
import { logoFont } from "@/utils/font"

export default function Page() {
  return (
    <div className="flex flex-col gap-8 min-h-svh w-full items-center justify-center p-6 md:p-10 bg-sky-400">
      <h1 className={`text-5xl text-white ${logoFont.className}`}>DayTrader Inc.</h1>
      <div className="w-full max-w-sm">
        <SignUpForm />
      </div>
    </div>
  )
}
