"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Area, AreaChart as RechartsArea, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";

export function AreaChart() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get("http://localhost:8090/api/users");
        const users = response.data;

        // Group users by weight categories
        const weightData = users.reduce((acc, user) => {
          // Define categories as ranges of 10 kg
          const weightCategory = `${Math.floor(user.poids / 10) * 10}-${Math.floor(user.poids / 10) * 10 + 9} kg`;

          // Find or create the category in the accumulated data
          const existingCategory = acc.find((item) => item.category === weightCategory);
          if (existingCategory) {
            existingCategory.users += 1; // Increment count for the category
          } else {
            acc.push({ category: weightCategory, users: 1 }); // Add new category
          }

          return acc;
        }, []);

        // Sort categories by range
        weightData.sort((a, b) => parseInt(a.category.split("-")[0]) - parseInt(b.category.split("-")[0]));

        setData(weightData);
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
        <CardTitle>Weight Distribution</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <RechartsArea data={data}>
            <XAxis dataKey="category" />
            <YAxis />
            <Tooltip />
            <Area
              type="monotone"
              dataKey="users"
              stroke="hsl(var(--chart-1))"
              fill="hsl(var(--chart-1))"
              fillOpacity={0.2}
            />
          </RechartsArea>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}
