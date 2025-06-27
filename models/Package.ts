import mongoose, { Schema, Document } from 'mongoose'
import { Package } from '@/types'

export interface IPackage extends Package, Document {}

const PackageSchema: Schema = new Schema({
  name: {
    type: String,
    required: [true, 'Package name is required'],
    trim: true,
    maxlength: [100, 'Name cannot exceed 100 characters']
  },
  description: {
    type: String,
    required: [true, 'Description is required'],
    trim: true
  },
  location: {
    type: String,
    required: [true, 'Location is required'],
    trim: true
  },
  duration: {
    type: Number,
    required: [true, 'Duration is required'],
    min: [1, 'Duration must be at least 1 day']
  },
  price: {
    type: Number,
    required: [true, 'Price is required'],
    min: [0, 'Price cannot be negative']
  },
  maxGroupSize: {
    type: Number,
    default: 15,
    min: [1, 'Group size must be at least 1']
  },
  difficulty: {
    type: String,
    enum: ['Easy', 'Moderate', 'Challenging'],
    default: 'Moderate'
  },
  includes: [{
    type: String,
    trim: true
  }],
  excludes: [{
    type: String,
    trim: true
  }],
  itinerary: [{
    day: {
      type: Number,
      required: true
    },
    title: {
      type: String,
      required: true,
      trim: true
    },
    description: {
      type: String,
      required: true,
      trim: true
    },
    activities: [{
      type: String,
      trim: true
    }]
  }],
  images: [{
    type: String,
    trim: true
  }],
  isActive: {
    type: Boolean,
    default: true
  }
}, {
  timestamps: true
})

// Create indexes
PackageSchema.index({ name: 1 })
PackageSchema.index({ location: 1 })
PackageSchema.index({ price: 1 })
PackageSchema.index({ isActive: 1 })

export default mongoose.models.Package || mongoose.model<IPackage>('Package', PackageSchema)