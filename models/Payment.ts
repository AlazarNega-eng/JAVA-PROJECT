import mongoose, { Schema, Document } from 'mongoose'
import { Payment } from '@/types'

export interface IPayment extends Payment, Document {}

const PaymentSchema: Schema = new Schema({
  bookingId: {
    type: Schema.Types.ObjectId,
    ref: 'Booking',
    required: [true, 'Booking ID is required'],
    unique: true
  },
  amount: {
    type: Number,
    required: [true, 'Amount is required'],
    min: [0, 'Amount cannot be negative']
  },
  paymentDate: {
    type: Date,
    default: Date.now
  },
  paymentMethod: {
    type: String,
    enum: ['Credit Card', 'Bank Transfer', 'Cash', 'Mobile Payment'],
    required: [true, 'Payment method is required']
  },
  status: {
    type: String,
    enum: ['Pending', 'Completed', 'Failed', 'Refunded'],
    default: 'Completed'
  },
  transactionId: {
    type: String,
    trim: true
  },
  notes: {
    type: String,
    trim: true
  }
}, {
  timestamps: true
})

// Create indexes
PaymentSchema.index({ bookingId: 1 })
PaymentSchema.index({ status: 1 })
PaymentSchema.index({ paymentDate: -1 })

export default mongoose.models.Payment || mongoose.model<IPayment>('Payment', PaymentSchema)