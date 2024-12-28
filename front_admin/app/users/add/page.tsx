"use client"

import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import * as z from "zod"
import { Button } from "@/components/ui/button"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { toast } from "sonner"
import axios from "axios"
import { useRouter } from "next/navigation"

const formSchema = z.object({
  name: z.string().min(2, "Name must be at least 2 characters"),
  email: z.string().email("Invalid email address"),
  sexe: z.string().min(1, "Please select a gender"),
  niveau: z.string().min(1, "Please select a level"),
})

export default function AddUserPage() {
  const router = useRouter()
  
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      email: "",
      sexe: "",
      niveau: "",
    },
  })

  // Function to generate random 6-digit password
  const generatePassword = () => {
    return Math.floor(100000 + Math.random() * 900000).toString();
  }

  async function onSubmit(values: z.infer<typeof formSchema>) {
    try {
      const randomPassword = generatePassword()
      
      // Prepare user data with generated password and default values
      const userData = {
        ...values,
        password: randomPassword,
        taille: 0,
        poids: 0,
        healthCondition: null,
        image_base64: null,
        image: null,
        objectifs: []
      }

      // Use the new addUser endpoint instead of signup
      await axios.post("http://localhost:8090/api/users/add", userData)
      
      toast.success(
        <div className="space-y-2">
          <p>User created successfully!</p>
          <p className="text-sm font-mono bg-slate-100 p-2 rounded">
            Temporary password: {randomPassword}
          </p>
          <p className="text-sm text-slate-500">
            Please share this password with the user securely.
          </p>
        </div>,
        {
          duration: 10000,
        }
      )
      
      form.reset()
      router.push('/users')
    } catch (error) {
      console.error("Error creating user:", error)
      toast.error(error.response?.data || "Error creating user")
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Add User</h1>
      
      <Card className="max-w-2xl">
        <CardHeader>
          <CardTitle>New User Details</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Name</FormLabel>
                    <FormControl>
                      <Input placeholder="Username" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input placeholder="user@example.com" type="email" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              
              <FormField
                control={form.control}
                name="sexe"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Gender</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select gender" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="Male">Male</SelectItem>
                        <SelectItem value="Female">Female</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="niveau"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Level</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Select level" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="beginner">Beginner</SelectItem>
                        <SelectItem value="intermediate">Intermediate</SelectItem>
                        <SelectItem value="advanced">Advanced</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Button type="submit">Create User</Button>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  )
}