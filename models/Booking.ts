import mongoose, { Schema, Document } from 'mongoose'
import { Booking } from '@/types'

export interface IBooking extends Booking, Document {}

const BookingSchema: Schema = new Schema({
  touristId: {
    type: Schema.Types.ObjectId,
    ref: 'Tourist',
    required: [true, 'Tourist ID is required']
  },
  packageId: {
    type: Schema.Types.ObjectId,
    ref: 'Package',
    required: [true, 'Package ID is required']
  },
  guideId: {
    type: Schema.Types.ObjectId,
    ref: 'Guide'
  },
  hotelIds: [{
    type: Schema.Types.ObjectId,
    ref: 'Hotel'
  }],
  bookingDate: {
    type: Date,
    default: Date.now
  },
  travelDate: {
    type: Date,
    required: [true, 'Travel date is required']
  },
  totalCost: {
    type: Number,
    required: [true, 'Total cost is required'],
    min: [0, 'Total cost cannot be negative']
  },
  status: {
    type: String,
    enum: ['Pending', 'Confirmed', 'Cancelled', 'Completed'],
    default: 'Pending'
  },
  numberOfTravelers: {
    type: Number,
    required: [true, 'Number of travelers is required'],
    min: [1, 'Must have at least 1 traveler']
  },
  specialRequests: {
    type: String,
    trim: true
  }
}, {
  timestamps: true
})

// Create indexes
BookingSchema.index({ touristId: 1 })
BookingSchema.index({ packageId: 1 })
BookingSchema.index({ status: 1 })
BookingSchema.index({ travelDate: 1 })
BookingSchema.index({ bookingDate: -1 })

export default mongoose.models.Booking || mongoose.model<IBooking>('Booking', BookingSchema)