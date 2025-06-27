import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Calendar } from 'lucide-react'

export function RecentBookings() {
  const bookings = [
    {
      id: 'BK001',
      tourist: 'Sarah Johnson',
      package: 'Lalibela Rock Churches',
      date: '2024-01-15',
      status: 'Confirmed',
      amount: 'ETB 15,500'
    },
    {
      id: 'BK002',
      tourist: 'Michael Chen',
      package: 'Simien Mountains Trek',
      date: '2024-01-18',
      status: 'Pending',
      amount: 'ETB 22,800'
    },
    {
      id: 'BK003',
      tourist: 'Emma Wilson',
      package: 'Axum Historical Tour',
      date: '2024-01-20',
      status: 'Confirmed',
      amount: 'ETB 18,200'
    }
  ]

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Calendar className="w-5 h-5" />
          Recent Bookings
        </CardTitle>
        <CardDescription>Latest booking activities</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {bookings.map((booking) => (
            <div key={booking.id} className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <p className="font-medium text-gray-900">{booking.tourist}</p>
                <p className="text-sm text-gray-600">{booking.package}</p>
                <p className="text-xs text-gray-500">{booking.date}</p>
              </div>
              <div className="text-right">
                <Badge 
                  variant={booking.status === 'Confirmed' ? 'default' : 'secondary'}
                  className="mb-1"
                >
                  {booking.status}
                </Badge>
                <p className="text-sm font-medium">{booking.amount}</p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}