import { twMerge } from "tailwind-merge";

interface CardProps {
    children: React.ReactNode;
    className?: string;
}

export const Card = ({children, className}: CardProps) => {
    return <div className={twMerge("border border-neutral-300 p-8 rounded-md", className)}>
        {children}
    </div>
}