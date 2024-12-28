"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Line, LineChart as RechartsLine, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";

export function LineChart() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get("http://localhost:8090/api/users");
        const users = response.data;

        // Group height data by gender
        const groupedData = users.reduce((acc, user) => {
          // Define categories as ranges of 10 cm
          const heightCategory = `${Math.floor(user.taille / 10) * 10}-${Math.floor(user.taille / 10) * 10 + 9} cm`;

          // Find or create the category in the accumulated data
          let existingCategory = acc.find((item) => item.category === heightCategory);
          if (!existingCategory) {
            existingCategory = { category: heightCategory, male: 0, female: 0 };
            acc.push(existingCategory);
          }

          // Increment the count for the corresponding gender
          if (user.sexe === "Male") {
            existingCategory.male += 1;
          } else if (user.sexe === "Female") {
            existingCategory.female += 1;
          }

          return acc;
        }, []);

        // Sort categories by range
        groupedData.sort((a, b) => parseInt(a.category.split("-")[0]) - parseInt(b.category.split("-")[0]));

        setData(groupedData);
      } catch (error) {
        console.error("Error fetching user data:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Height Distribution by Gender</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <RechartsLine data={data}>
            <XAxis dataKey="category" />
            <YAxis />
            <Tooltip />
            <Line
              type="monotone"
              dataKey="male"
              stroke="hsl(var(--chart-1))" // Color for Male
              strokeWidth={2}
            />
            <Line
              type="monotone"
              dataKey="female"
              stroke="hsl(var(--chart-2))" // Color for Female
              strokeWidth={2}
            />
          </RechartsLine>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}
