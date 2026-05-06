# ============================================================
# STAGE 1: BUILD
# Use Node.js to install dependencies and build the Vue app
# ============================================================
FROM node:20-alpine AS build

# Set working directory
WORKDIR /app

# Copy package files first (for dependency caching)
COPY package*.json ./

# Install all npm dependencies
RUN npm install

# Copy the rest of the source code
COPY . .

# Build the Vue.js app for production
# This creates a /app/dist folder with optimized static files
RUN npm run build

# ============================================================
# STAGE 2: SERVE
# Use Nginx to serve the built static files
# Nginx is a fast, lightweight web server — perfect for Vue apps
# ============================================================
FROM nginx:stable-alpine

# Copy the built Vue app from Stage 1 into Nginx's web root
COPY --from=build /app/dist /usr/share/nginx/html

# Copy our custom Nginx config (handles Vue Router properly)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 (standard HTTP port)
EXPOSE 80

# Nginx starts automatically — no extra command needed
CMD ["nginx", "-g", "daemon off;"]
