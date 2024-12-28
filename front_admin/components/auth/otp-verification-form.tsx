"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSlot,
} from "@/components/ui/input-otp";
import { ArrowLeft, Loader2 } from "lucide-react";

interface OtpVerificationFormProps {
  email: string;
  onBack: () => void;
  onVerify: () => void;
}

export function OtpVerificationForm({
  email,
  onBack,
  onVerify,
}: OtpVerificationFormProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [value, setValue] = useState("");

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (value.length !== 6) return;

    setIsLoading(true);
    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1000));
    setIsLoading(false);
    onVerify();
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <p className="text-sm text-muted-foreground text-center">
        We've sent a verification code to {email}
      </p>
      <div className="flex justify-center">
        <InputOTP
          value={value}
          onChange={(value) => setValue(value)}
          maxLength={6}
          render={({ slots }) => (
            <InputOTPGroup>
              {slots.map((slot, index) => (
                <InputOTPSlot key={index} {...slot} />
              ))}
            </InputOTPGroup>
          )}
        />
      </div>
      <Button
        type="submit"
        className="w-full"
        disabled={isLoading || value.length !== 6}
      >
        {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
        Verify code
      </Button>
      <Button
        type="button"
        variant="ghost"
        className="w-full"
        onClick={onBack}
      >
        <ArrowLeft className="mr-2 h-4 w-4" />
        Back
      </Button>
    </form>
  );
}