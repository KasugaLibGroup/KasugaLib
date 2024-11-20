import {DefaultTheme} from "vitepress";
export const GuideSideBar : DefaultTheme.SidebarItem[] = [
    {
        text: '起步指南',
        items:[
            {
                text: "介绍",
                link: "/guide/"
            }
        ]
    },
    {
        text: 'Javascript Addons 开发',
        items:[
            {
                text: "介绍",
                link: "/guide/addons/"
            },
            {
                text: "初始化开发环境",
                link: "/guide/addons/initialization"
            },
            {
                text: "编写你的第一个 Addon",
            }
        ]
    }
]
export default GuideSideBar;