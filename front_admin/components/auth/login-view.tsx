"use client";

import { useState } from "react";
import { LoginForm } from "./login-form";
import { ForgotPasswordForm } from "./forgot-password-form";
import { OtpVerificationForm } from "./otp-verification-form";
import { ResetPasswordForm } from "./reset-password-form";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { LockKeyhole } from "lucide-react";

type AuthView = "login" | "forgot-password" | "otp-verification" | "reset-password";

export function LoginView() {
  const [view, setView] = useState<AuthView>("login");
  const [email, setEmail] = useState("");

  return (
    <Card className="w-full max-w-[400px]">
      <CardHeader className="space-y-1 flex flex-col items-center">
        <div className="bg-primary/10 p-3 rounded-full">
          <LockKeyhole className="h-6 w-6 text-primary" />
        </div>
        <CardTitle className="text-2xl text-center">
          {view === "login" && "Welcome back"}
          {view === "forgot-password" && "Reset password"}
          {view === "otp-verification" && "Enter verification code"}
          {view === "reset-password" && "Set new password"}
        </CardTitle>
      </CardHeader>
      <CardContent>
        {view === "login" && (
          <LoginForm onForgotPassword={() => setView("forgot-password")} />
        )}
        {view === "forgot-password" && (
          <ForgotPasswordForm
            onBack={() => setView("login")}
            onSubmit={(email) => {
              setEmail(email);
              setView("otp-verification");
            }}
          />
        )}
        {view === "otp-verification" && (
          <OtpVerificationForm
            email={email}
            onBack={() => setView("forgot-password")}
            onVerify={() => setView("reset-password")}
          />
        )}
        {view === "reset-password" && (
          <ResetPasswordForm
            onSuccess={() => setView("login")}
          />
        )}
      </CardContent>
    </Card>
  );
}