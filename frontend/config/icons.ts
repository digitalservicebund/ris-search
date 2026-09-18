import { FileSystemIconLoader } from "unplugin-icons/loaders";
import Icons from "unplugin-icons/vite";

export const icons = Icons({
  scale: 1.5,
  customCollections: {
    custom: FileSystemIconLoader("./src/assets/icons"),
  },
  iconCustomizer(collection, icon, props) {
    props.role = "presentation";
  },
});
