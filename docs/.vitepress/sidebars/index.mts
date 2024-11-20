import {DefaultTheme} from "vitepress/theme";
import GuideSideBar from './guide.mjs'
export const sidebars: DefaultTheme.Config['sidebar'] = {
    "/guide/": GuideSideBar
}

export default sidebars;