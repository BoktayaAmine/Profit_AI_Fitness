"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Cell, Pie, PieChart as RechartsPie, ResponsiveContainer, Tooltip } from "recharts";

const COLORS = [
  "hsl(var(--chart-3))", // Color for Niveau 1
  "hsl(var(--chart-4))", // Color for Niveau 2
  "hsl(var(--chart-5))", // Color for Niveau 3
  "hsl(var(--chart-6))", // Color for Niveau 4
];

export function PieChart() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get("http://localhost:8090/api/users");
        const users = response.data;

        // Count users by 'niveau'
        const niveauCounts = users.reduce(
          (acc, user) => {
            if (user.niveau) {
              acc[user.niveau] = acc[user.niveau] ? acc[user.niveau] + 1 : 1;
            }
            return acc;
          },
          {}
        );

        // Transform data for the PieChart
        const chartData = Object.keys(niveauCounts).map((niveau) => ({
          name: niveau,
          value: niveauCounts[niveau],
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
        <CardTitle>User Distribution by Niveau</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <RechartsPie>
            <Pie
              data={data}
              cx="50%"
              cy="50%"
              innerRadius={60}
              outerRadius={80}
              paddingAngle={5}
              dataKey="value"
            >
              {data.map((_, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip />
          </RechartsPie>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  );
}
