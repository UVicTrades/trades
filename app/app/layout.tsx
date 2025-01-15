import NavBar from "@/components/NavBar";
export default function Layout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <>
      <NavBar/>
      <div className="flex flex-col md:px-48 px-8 py-8 gap-y-4">
        {children}
      </div>
    </>
  );
}
