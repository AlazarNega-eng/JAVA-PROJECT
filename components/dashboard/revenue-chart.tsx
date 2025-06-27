'use client'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { TrendingUp } from 'lucide-react'

export function RevenueChart() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <TrendingUp className="w-5 h-5" />
          Revenue Overview
        </CardTitle>
        <CardDescription>Monthly revenue for the past 6 months</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="h-64 flex items-end justify-between space-x-2">
          {[420000, 380000, 450000, 520000, 485000, 540000].map((value, index) => (
            <div key={index} className="flex flex-col items-center space-y-2">
              <div
                className="bg-ethiopian-green rounded-t w-12 transition-all hover:bg-green-700"
                style={{ height: `${(value / 600000) * 200}px` }}
              />
              <span className="text-xs text-gray-600">
                {['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'][index]}
              </span>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}