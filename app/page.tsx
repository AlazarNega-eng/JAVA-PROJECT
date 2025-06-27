import Link from 'next/link'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { MapPin, Users, Calendar, CreditCard, Hotel, UserCheck } from 'lucide-react'

export default function HomePage() {
  const features = [
    {
      icon: Users,
      title: 'Tourist Management',
      description: 'Manage tourist registrations, profiles, and travel history',
      href: '/dashboard/tourists'
    },
    {
      icon: UserCheck,
      title: 'Guide Management',
      description: 'Organize tour guides, specializations, and assignments',
      href: '/dashboard/guides'
    },
    {
      icon: Hotel,
      title: 'Hotel Management',
      description: 'Maintain hotel information, bookings, and availability',
      href: '/dashboard/hotels'
    },
    {
      icon: MapPin,
      title: 'Package Management',
      description: 'Create and manage tour packages for Ethiopian heritage sites',
      href: '/dashboard/packages'
    },
    {
      icon: Calendar,
      title: 'Booking System',
      description: 'Process bookings, manage confirmations, and track status',
      href: '/dashboard/bookings'
    },
    {
      icon: CreditCard,
      title: 'Payment Processing',
      description: 'Handle payments, track transactions, and manage billing',
      href: '/dashboard/payments'
    }
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 via-yellow-50 to-red-50">
      {/* Header */}
      <header className="border-b bg-white/80 backdrop-blur-sm">
        <div className="container mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-full ethiopian-gradient flex items-center justify-center">
                <MapPin className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">Ethiopian Tourism</h1>
                <p className="text-sm text-gray-600">Heritage Management System</p>
              </div>
            </div>
            <Link href="/dashboard">
              <Button className="bg-ethiopian-green hover:bg-green-700">
                Access Dashboard
              </Button>
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <section className="py-20">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-5xl font-bold text-gray-900 mb-6">
            Discover Ethiopia's Rich Heritage
          </h2>
          <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
            Comprehensive tourism management platform designed to showcase and manage 
            Ethiopia's incredible cultural heritage, historical sites, and natural wonders.
          </p>
          <div className="flex justify-center space-x-4">
            <Link href="/dashboard">
              <Button size="lg" className="bg-ethiopian-green hover:bg-green-700">
                Get Started
              </Button>
            </Link>
            <Button size="lg" variant="outline">
              Learn More
            </Button>
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section className="py-16 bg-white/50">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">
              Complete Tourism Management Solution
            </h3>
            <p className="text-lg text-gray-600 max-w-2xl mx-auto">
              Everything you need to manage tourism operations efficiently and effectively
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((feature, index) => (
              <Card key={index} className="hover:shadow-lg transition-shadow duration-300">
                <CardHeader>
                  <div className="w-12 h-12 rounded-lg bg-ethiopian-green/10 flex items-center justify-center mb-4">
                    <feature.icon className="w-6 h-6 text-ethiopian-green" />
                  </div>
                  <CardTitle className="text-xl">{feature.title}</CardTitle>
                  <CardDescription>{feature.description}</CardDescription>
                </CardHeader>
                <CardContent>
                  <Link href={feature.href}>
                    <Button variant="outline" className="w-full">
                      Manage {feature.title.split(' ')[0]}
                    </Button>
                  </Link>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Ethiopian Heritage Showcase */}
      <section className="py-16">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h3 className="text-3xl font-bold text-gray-900 mb-4">
              Ethiopian Heritage Highlights
            </h3>
            <p className="text-lg text-gray-600">
              Explore some of Ethiopia's most treasured destinations
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="overflow-hidden">
              <div className="h-48 bg-gradient-to-br from-orange-400 to-red-500"></div>
              <CardHeader>
                <CardTitle>Lalibela Rock Churches</CardTitle>
                <CardDescription>
                  UNESCO World Heritage site featuring 11 medieval monolithic churches
                </CardDescription>
              </CardHeader>
            </Card>
            
            <Card className="overflow-hidden">
              <div className="h-48 bg-gradient-to-br from-green-400 to-blue-500"></div>
              <CardHeader>
                <CardTitle>Simien Mountains</CardTitle>
                <CardDescription>
                  Dramatic landscapes and endemic wildlife in Africa's highest peaks
                </CardDescription>
              </CardHeader>
            </Card>
            
            <Card className="overflow-hidden">
              <div className="h-48 bg-gradient-to-br from-yellow-400 to-orange-500"></div>
              <CardHeader>
                <CardTitle>Axum Obelisks</CardTitle>
                <CardDescription>
                  Ancient stelae marking the heart of the Aksumite Empire
                </CardDescription>
              </CardHeader>
            </Card>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-gray-900 text-white py-12">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <div className="flex items-center space-x-3 mb-4">
                <div className="w-8 h-8 rounded-full ethiopian-gradient flex items-center justify-center">
                  <MapPin className="w-4 h-4 text-white" />
                </div>
                <span className="font-bold">Ethiopian Tourism</span>
              </div>
              <p className="text-gray-400">
                Preserving and promoting Ethiopia's rich cultural heritage through modern technology.
              </p>
            </div>
            
            <div>
              <h4 className="font-semibold mb-4">Quick Links</h4>
              <ul className="space-y-2 text-gray-400">
                <li><Link href="/dashboard" className="hover:text-white">Dashboard</Link></li>
                <li><Link href="/dashboard/packages" className="hover:text-white">Tour Packages</Link></li>
                <li><Link href="/dashboard/bookings" className="hover:text-white">Bookings</Link></li>
              </ul>
            </div>
            
            <div>
              <h4 className="font-semibold mb-4">Management</h4>
              <ul className="space-y-2 text-gray-400">
                <li><Link href="/dashboard/tourists" className="hover:text-white">Tourists</Link></li>
                <li><Link href="/dashboard/guides" className="hover:text-white">Guides</Link></li>
                <li><Link href="/dashboard/hotels" className="hover:text-white">Hotels</Link></li>
              </ul>
            </div>
            
            <div>
              <h4 className="font-semibold mb-4">Contact</h4>
              <p className="text-gray-400">
                Ethiopian Tourism Management<br />
                Addis Ababa, Ethiopia<br />
                info@ethiopiantourism.com
              </p>
            </div>
          </div>
          
          <div className="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
            <p>&copy; 2024 Ethiopian Tourism Management System. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  )
}