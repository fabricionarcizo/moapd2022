import SwiftUI
import shared

struct CheckboxField: View {
    let id: String
    let label: String
    let size: CGFloat
    let color: Color
    let textSize: Int
    let callback: (String, Bool)->()

    init(
        id: String,
        label:String,
        size: CGFloat = 10,
        color: Color = Color.black,
        textSize: Int = 14,
        callback: @escaping (String, Bool)->()
        ) {
        self.id = id
        self.label = label
        self.size = size
        self.color = color
        self.textSize = textSize
        self.callback = callback
    }

    @State var isMarked:Bool = false
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        Button(action:{
            self.isMarked.toggle()
            self.callback(self.id, self.isMarked)
        }) {
            HStack(alignment: .center, spacing: 10) {
                Image(systemName: self.isMarked ? "checkmark.square" : "square")
                    .renderingMode(.original)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: self.size, height: self.size)
                Text(label)
            }.foregroundColor(colorScheme == .dark ? Color.white : self.color)
        }
        .foregroundColor(colorScheme == .dark ? Color.black : Color.white)
    }
}

struct ContentView: View {

    @State var isMarked = false

    let interaction = LastInteraction()

    @State private var num: String = "-1"
    private var text: String {
        if let num = Int32(num) {
            return interaction.interactionText(num: num, status: isMarked)
        } else {
            return "Hello World!"
        }
    }

    var body: some View {
        VStack(alignment: .center) {
            Text(text)
                .frame(height: 75)
            HStack(alignment: .center) {
                Button("True") {
                    num = "0"
                }.frame(width: 50)
                Button("False") {
                    num = "1"
                }.frame(width: 50)
            }
            CheckboxField(
                id: "checkbox",
                label: "Select this option",
                size: 14,
                textSize: 14,
                callback: checkboxSelected
            )
        }
    }

    func checkboxSelected(id: String, isMarked: Bool) {
        num = "2"
        self.isMarked = isMarked
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
