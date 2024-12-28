# Build stage
FROM node:16-alpine AS builder

# Set working directory
WORKDIR /app

# Add environment variables to ignore TypeScript errors
ENV NEXT_TELEMETRY_DISABLED=1
ENV CI=false

# First copy only package files and install dependencies
COPY package*.json ./
RUN npm ci

# Then copy the source code
COPY . .

# Build the application with --no-lint flag
RUN npm run build -- --no-lint

# Production stage
FROM node:16-alpine AS runner

WORKDIR /app

# Copy only necessary files from builder
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules

# Expose port
EXPOSE 3000

# Start the application
CMD ["npm", "start"]