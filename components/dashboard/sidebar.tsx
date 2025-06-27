'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { cn } from '@/lib/utils'
import { 
  LayoutDashboard,
  Users,
  UserCheck,
  Building2,
  MapPin,
  Calendar,
  CreditCard,
  Settings,
  BarChart3
} from 'lucide-react'

const navigation = [
  {
    name: 'Dashboard',
    href: '/dashboard',
    icon: LayoutDashboard,
  },
  {
    name: 'Tourists',
    href: '/dashboard/tourists',
    icon: Users,
  },
  {
    name: 'Guides',
    href: '/dashboard/guides',
    icon: UserCheck,
  },
  {
    name: 'Hotels',
    href: '/dashboard/hotels',
    icon: Building2,
  },
  {
    name: 'Packages',
    href: '/dashboard/packages',
    icon: MapPin,
  },
  {
    name: 'Bookings',
    href: '/dashboard/bookings',
    icon: Calendar,
  },
  {
    name: 'Payments',
    href: '/dashboard/payments',
    icon: CreditCard,
  },
  {
    name: 'Analytics',
    href: '/dashboard/analytics',
    icon: BarChart3,
  },
  {
    name: 'Settings',
    href: '/dashboard/settings',
    icon: Settings,
  },
]

export function Sidebar() {
  const pathname = usePathname()

  return (
    <div className="w-64 bg-white border-r border-gray-200 min-h-screen">
      <div className="p-6">
        <div className="flex items-center space-x-3">
          <div className="w-8 h-8 rounded-full ethiopian-gradient flex items-center justify-center">
            <MapPin className="w-4 h-4 text-white" />
          </div>
          <div>
            <h2 className="font-bold text-gray-900">Ethiopian Tourism</h2>
            <p className="text-xs text-gray-600">Management System</p>
          </div>
        </div>
      </div>
      
      <nav className="px-3 space-y-1">
        {navigation.map((item) => {
          const isActive = pathname === item.href
          return (
            <Link
              key={item.name}
              href={item.href}
              className={cn(
                'flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors',
                isActive
                  ? 'bg-ethiopian-green text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              )}
            >
              <item.icon className="w-5 h-5 mr-3" />
              {item.name}
            </Link>
          )
        })}
      </nav>
    </div>
  )
}