import "./globals.css";
import type { Metadata } from "next";
import { inter } from "@/utils/font";
import { UserProvider } from "@/contexts/UserContext";

export const metadata: Metadata = {
  title: "Day Trader",
  description: "An app to make a million dollars on GME",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${inter.className}`}
      >
        <UserProvider>
          {children}
        </UserProvider>
      </body>
    </html>
  );
}
