import mongoose, { Schema, Document } from 'mongoose'
import { Guide } from '@/types'

export interface IGuide extends Guide, Document {}

const GuideSchema: Schema = new Schema({
  name: {
    type: String,
    required: [true, 'Name is required'],
    trim: true,
    maxlength: [100, 'Name cannot exceed 100 characters']
  },
  contact: {
    type: String,
    required: [true, 'Contact information is required'],
    trim: true
  },
  language: {
    type: String,
    required: [true, 'Language is required'],
    trim: true
  },
  experience: {
    type: String,
    required: [true, 'Experience is required'],
    trim: true
  },
  rating: {
    type: Number,
    min: [1, 'Rating must be at least 1'],
    max: [5, 'Rating cannot exceed 5'],
    default: 5
  },
  isAvailable: {
    type: Boolean,
    default: true
  }
}, {
  timestamps: true
})

// Create indexes
GuideSchema.index({ name: 1 })
GuideSchema.index({ isAvailable: 1 })
GuideSchema.index({ rating: -1 })

export default mongoose.models.Guide || mongoose.model<IGuide>('Guide', GuideSchema)