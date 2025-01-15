'use client'

import { Card } from "@/components/Card";
import useUser from "@/hooks/useUser";

export default function Home() {
  const {user} = useUser();
  return (
    <Card className="bg-white w-100 h-100">
      <h2 className="text-3xl font-bold mb-4">Dashboard</h2>
      <p>{user?.name}</p>
      <div className="grid md:grid-cols-4 gap-8">
        <Card className="flex flex-col justify-center">
          <p>Your balance:</p>
          <p className="font-bold text-2xl">$794.82</p>
          <p className="text-sm">^ 11%</p>
        </Card>
        <Card className="flex flex-col col-span-3 row-span-3">
          <p className="font-bold text-xl">Your Stocks:</p>
          <ul className="p-4 border border-gray-200 rounded-md flex flex-col gap-y-2">
            {user?.stocks.map((stock) => (
              <li key={stock.ticker} className="flex flex-row justify-between">
                <p className="font-semibold">{stock.name}</p>
                <p>{stock.price}</p>
              </li>
            ))}
          </ul>
        </Card>
      </div>
    </Card>
  );
}
