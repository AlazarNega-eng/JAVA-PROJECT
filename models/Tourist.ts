import mongoose, { Schema, Document } from 'mongoose'
import { Tourist } from '@/types'

export interface ITourist extends Tourist, Document {}

const TouristSchema: Schema = new Schema({
  name: {
    type: String,
    required: [true, 'Name is required'],
    trim: true,
    maxlength: [100, 'Name cannot exceed 100 characters']
  },
  email: {
    type: String,
    required: [true, 'Email is required'],
    unique: true,
    lowercase: true,
    match: [/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/, 'Please enter a valid email']
  },
  phone: {
    type: String,
    required: [true, 'Phone number is required'],
    trim: true
  },
  address: {
    type: String,
    required: [true, 'Address is required'],
    trim: true
  },
  nationality: {
    type: String,
    required: [true, 'Nationality is required'],
    trim: true
  }
}, {
  timestamps: true
})

// Create indexes
TouristSchema.index({ email: 1 })
TouristSchema.index({ name: 1 })

export default mongoose.models.Tourist || mongoose.model<ITourist>('Tourist', TouristSchema)