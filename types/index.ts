export interface Tourist {
  _id?: string
  name: string
  email: string
  phone: string
  address: string
  nationality: string
  createdAt?: Date
  updatedAt?: Date
}

export interface Guide {
  _id?: string
  name: string
  contact: string
  language: string
  experience: string
  rating?: number
  isAvailable?: boolean
  createdAt?: Date
  updatedAt?: Date
}

export interface Hotel {
  _id?: string
  name: string
  location: string
  contact: string
  rating?: number
  amenities?: string[]
  priceRange?: {
    min: number
    max: number
  }
  createdAt?: Date
  updatedAt?: Date
}

export interface Package {
  _id?: string
  name: string
  description: string
  location: string
  duration: number
  price: number
  maxGroupSize?: number
  difficulty?: 'Easy' | 'Moderate' | 'Challenging'
  includes?: string[]
  excludes?: string[]
  itinerary?: {
    day: number
    title: string
    description: string
    activities: string[]
  }[]
  images?: string[]
  isActive?: boolean
  createdAt?: Date
  updatedAt?: Date
}

export interface Booking {
  _id?: string
  touristId: string
  packageId: string
  guideId?: string
  hotelIds?: string[]
  bookingDate: Date
  travelDate: Date
  totalCost: number
  status: 'Pending' | 'Confirmed' | 'Cancelled' | 'Completed'
  numberOfTravelers: number
  specialRequests?: string
  createdAt?: Date
  updatedAt?: Date
}

export interface Payment {
  _id?: string
  bookingId: string
  amount: number
  paymentDate: Date
  paymentMethod: 'Credit Card' | 'Bank Transfer' | 'Cash' | 'Mobile Payment'
  status: 'Pending' | 'Completed' | 'Failed' | 'Refunded'
  transactionId?: string
  notes?: string
  createdAt?: Date
  updatedAt?: Date
}

export interface DashboardStats {
  totalTourists: number
  totalBookings: number
  totalRevenue: number
  activePackages: number
  pendingBookings: number
  completedBookings: number
  monthlyRevenue: number[]
  popularPackages: {
    name: string
    bookings: number
  }[]
}