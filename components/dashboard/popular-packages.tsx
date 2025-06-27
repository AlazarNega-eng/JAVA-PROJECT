import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { MapPin, Star } from 'lucide-react'

export function PopularPackages() {
  const packages = [
    {
      name: 'Lalibela Rock Churches',
      bookings: 45,
      rating: 4.9,
      revenue: 'ETB 697,500'
    },
    {
      name: 'Simien Mountains Trek',
      bookings: 38,
      rating: 4.8,
      revenue: 'ETB 866,400'
    },
    {
      name: 'Axum Historical Tour',
      bookings: 32,
      rating: 4.7,
      revenue: 'ETB 582,400'
    },
    {
      name: 'Danakil Depression',
      bookings: 28,
      rating: 4.6,
      revenue: 'ETB 784,000'
    }
  ]

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <MapPin className="w-5 h-5" />
          Popular Packages
        </CardTitle>
        <CardDescription>Most booked tour packages this month</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {packages.map((pkg, index) => (
            <div key={index} className="flex items-center justify-between p-3 border rounded-lg">
              <div className="flex-1">
                <p className="font-medium text-gray-900">{pkg.name}</p>
                <div className="flex items-center space-x-4 mt-1">
                  <span className="text-sm text-gray-600">{pkg.bookings} bookings</span>
                  <div className="flex items-center">
                    <Star className="w-3 h-3 text-yellow-400 fill-current" />
                    <span className="text-sm text-gray-600 ml-1">{pkg.rating}</span>
                  </div>
                </div>
              </div>
              <div className="text-right">
                <p className="text-sm font-medium text-gray-900">{pkg.revenue}</p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  )
}