import { UserType } from "./types";

export const exampleUser: UserType = {
    userID: "123",
    username: "skenning",
    name: "Scott Kenning",
    email: "email@email.email",
    balance: 1254.66,
    stocks: [
        {name: "Apple", ticker: "AAPL", price: 145.12},
        {name: "Microsoft", ticker: "MSFT", price: 245.12},
        {name: "Tesla", ticker: "TSLA", price: 645.12},
    ]
}