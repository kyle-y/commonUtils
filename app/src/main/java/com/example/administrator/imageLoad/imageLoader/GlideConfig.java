package com.example.administrator.imageLoad.imageLoader;

/**
 * 可以在这里进行glide的配置
 */
//
//@GlideModule
//public class GlideConfig extends AppGlideModule {
//
//    @Override
//    public boolean isManifestParsingEnabled() {
//        return false;
//    }
//
//    @Override
//    public void applyOptions(Context context, GlideBuilder builder) {
//        super.applyOptions(context, builder);
//        MemorySizeCalculator memorySizeCalculator = new MemorySizeCalculator.Builder(context).build();
//        int defaultMemoryCacheSize = memorySizeCalculator.getMemoryCacheSize();
//        int defaultBitmapPoolSize = memorySizeCalculator.getBitmapPoolSize();
//        int maxMemory = (int) Runtime.getRuntime().maxMemory();
//        int memoryCacheSize = maxMemory / 8;
//
//        builder.setMemoryCache(new LruResourceCache(defaultMemoryCacheSize));
//        builder.setBitmapPool(new LruBitmapPool(defaultBitmapPoolSize));
//
//        int diskCacheSize = 1024 * 1024 * 300;
//        builder.setDiskCache(new DiskLruCacheFactory(CacheUtil.imagePath,"glide",diskCacheSize));
//    }
//
//    @Override
//    public void registerComponents(Context context, Glide glide, Registry registry) {
//        super.registerComponents(context, glide, registry);
//    }
//}