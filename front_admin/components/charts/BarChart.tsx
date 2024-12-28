"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Bar, BarChart as RechartsBar, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";

export function BarChart() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get("http://localhost:8090/api/users");
        const users = response.data;

        // Count users by health condition
        const healthConditionCounts = users.reduce((acc, user) => {
          const condition = user.healthCondition; // Adjust this field based on your User model
          acc[condition] = (acc[condition] || 0) + 1;
          return acc;
        }, {});

        // Transform data for the BarChart
        const chartData = Object.entries(healthConditionCounts).map(([name, users]) => ({
          name,
          users,
        }));

        setData(chartData);
      } catch (error) {
        console.error("Error fetching users:", error);
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
        <CardTitle>Health Conditions</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="101%" height={300}>
          <RechartsBar data={data} barGap={300}>
           <XAxis dataKey="name" interval={0} />
           <YAxis />
            <Tooltip />
            <Bar dataKey="users" fill="hsl(var(--chart-2))" />
          </RechartsBar>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}
