import mongoose, { Schema, Document } from 'mongoose'
import { Hotel } from '@/types'

export interface IHotel extends Hotel, Document {}

const HotelSchema: Schema = new Schema({
  name: {
    type: String,
    required: [true, 'Hotel name is required'],
    trim: true,
    maxlength: [100, 'Name cannot exceed 100 characters']
  },
  location: {
    type: String,
    required: [true, 'Location is required'],
    trim: true
  },
  contact: {
    type: String,
    required: [true, 'Contact information is required'],
    trim: true
  },
  rating: {
    type: Number,
    min: [1, 'Rating must be at least 1'],
    max: [5, 'Rating cannot exceed 5']
  },
  amenities: [{
    type: String,
    trim: true
  }],
  priceRange: {
    min: {
      type: Number,
      min: [0, 'Minimum price cannot be negative']
    },
    max: {
      type: Number,
      min: [0, 'Maximum price cannot be negative']
    }
  }
}, {
  timestamps: true
})

// Create indexes
HotelSchema.index({ name: 1 })
HotelSchema.index({ location: 1 })
HotelSchema.index({ rating: -1 })

export default mongoose.models.Hotel || mongoose.model<IHotel>('Hotel', HotelSchema)