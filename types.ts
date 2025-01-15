export interface UserType {
  userID: string;
  username: string;
  name: string;
  email: string;
  balance: number;
  stocks: Stock[];
}

export interface Stock {
  name: string;
  ticker: string;
  price: number;
}

export interface UserContextType {
  user: UserType | null;
  setUserInStorage: (user: UserType, token: string) => void;
  logout: () => void;
}