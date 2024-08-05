import { defineConfig } from 'vitepress'
import SideBarConfiguration from './sidebars/index.mjs'

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "KasugaLib",
  description: "A universal Minecraft Forge Mod Development Framework",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Examples', link: '/markdown-examples' }
    ],

    sidebar: SideBarConfiguration,

    socialLinks: [
      { icon: 'github', link: 'https://github.com/vuejs/vitepress' }
    ]
  }
})
