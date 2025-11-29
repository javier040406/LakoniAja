package com.example.projek; // Pastikan package ini sesuai dengan struktur folder Anda

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArtikelFragment extends Fragment {

    RecyclerView rvArtikel;
    ArrayList<ArtikelModel> listArtikel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artikel, container, false);

        rvArtikel = view.findViewById(R.id.rvArtikel);

        listArtikel = new ArrayList<>();

        listArtikel.add(new ArtikelModel(
                "Pentingnya Kesehatan Mental Remaja",
                "Masa remaja adalah masa yang penting...",
                "Masa remaja adalah masa yang penting dalam pembentukan generasi akan datang yang sehat, tangguh, dan produktif. Untuk mewujudkannya, menjaga kesehatan fisik saja tidak cukup. Kesehatan mental remaja juga memainkan peranan penting dalam menentukan kualitas hidup dan kesejahteraan mereka. \n" +
                        "Namun, akhir-akhir ini masalah kesehatan mental di kalangan remaja semakin meningkat. Sebuah survei yang dilakukan oleh I-NAMHS (Indonesia National Adolescent Mental Health Survey) tahun 2022 menunjukkan sebanyak 15.5 juta atau sekitar 34.9% remaja mengalami masalah kesehatan mental. Kemudian, data dari WHO juga menunjukkan 1 di antara 7 anak berusia 10-19 tahun mengalami masalah kesehatan mental.\n" +
                        "Meski demikian, kesadaran terhadap pentingnya kesehatan mental bagi remaja ini, seharusnya tidak hanya menjadi tanggung jawab orang tua dan keluarga semata, tapi juga masyarakat dan pemerintah. Dibutuhkan peran aktif semua pihak dan berbagai institusi untuk mendukung kesejahteraan dan kesehatan mental remaja.\n" +
                        "1.1\tPeran Kesehatan Mental dalam Perkembangan Remaja\n" +
                        "Kesehatan mental yang baik dapat membantu remaja tumbuh kembang secara optimal, secara emosional, fisik dan sosial. Beberapa alasan pentingnya kesehatan mental yang baik bagi remaja adalah sebagai berikut:\n" +
                        " \n" +
                        "1. Membantu Membangun Hubungan yang Sehat \n" +
                        "Kesehatan mental yang baik membuat remaja mampu membangun hubungan yang kuat dengan keluarga, teman dan orang-orang di sekitarnya, serta menjadi bagian dari komunitas.\n" +
                        " \n" +
                        "2. Membantu Beradaptasi\n" +
                        "Mereka akan mampu beradaptasi dengan perubahan dan berbagai tantangan hidup. Mereka bisa bangkit kembali dari rasa kecewa dan kesal. \n" +
                        " \n" +
                        "3. Memiliki Rasa Percaya Diri Tinggi\n" +
                        "Mereka lebih menikmati hidup, merasa bahagia dengan dirinya sendiri, serta memiliki sikap positif dan rasa pencapaian.\n" +
                        " \n" +
                        "4. Mendukung Kesehatan Fisik\n" +
                        "Mereka akan menjadi lebih aktif dan sehat serta cukup beristirahat, sehingga mampu berkonsentrasi saat belajar, yang akan mendukung keberhasilannya dalam menyelesaikan pendidikan.\n" +
                        " \n" +
                        "2.1\tTanda-tanda Masalah Kesehatan Mental pada Remaja\n" +
                        "Gejala gangguan kesehatan mental seringkali diabaikan, karena dianggap sebagai perubahan yang normal terjadi di masa pubertas. Padahal, jika tidak ditangani dengan baik sejak dini, gejala-gejala umum ini bisa bertambah parah dan menjadi gejala gangguan kejiwaan yang berat, yang bahkan bisa berujung pada perilaku menyakiti diri atau bunuh diri.\n" +
                        " \n" +
                        "Berikut adalah tanda-tanda masalah kesehatan mental pada remaja, yang harus diwaspadai oleh orang tua dan orang lain di sekitarnya:\n" +
                        "1. Kesulitan Mengendalikan Emosi\n" +
                        "Remaja yang kesehatan mentalnya terganggu mengalami kesulitan mengelola emosinya. Ia menjadi lebih sensitif, bisa marah meledak-ledak atau merasa sedih berlebihan tanpa alasan yang jelas. \n" +
                        " \n" +
                        "2. Mengalami Perubahan Perilaku\n" +
                        "Jika anak remaja tiba-tiba mudah tersinggung, mengamuk, memberontak atau berperilaku seperti anak kecil, bisa saja ini merupakan tanda-tanda masalah kesehatan mental. Ia mungkin juga kehilangan minat terhadap hal-hal yang biasa ia lakukan, seperti pergi ke sekolah atau bermain bersama teman.\n" +
                        " \n" +
                        "3. Menarik Diri dari Lingkungan Sosial\n" +
                        "Tanda gangguan kesehatan mental lainnya adalah anak akan merasa cemas berlebihan saat berada di antara orang lain, dan takut terhadap penolakan, sehingga cenderung menarik diri dan menghindar dari keramaian.\n" +
                        " \n" +
                        "4. Kehilangan Rasa Percaya Diri\n" +
                        "Masalah kesehatan mental juga dapat membuat anak remaja merasa tidak berharga dan menyalahkan dirinya sendiri. Untuk mengembalikan rasa percaya dirinya, anak kadang melampiaskannya dengan melakukan hal-hal buruk, seperti merokok, mengkonsumsi minuman beralkohol atau obat-obatan terlarang.\n" +
                        " \n" +
                        "5. Prestasi Menurun\n" +
                        "Hilangnya minat terhadap sekolah dan aktivitas lainnya dapat mengganggu kemampuannya belajar. Kondisi ini juga akan melemahkan kemampuan kognitifnya, seperti berpikir, mengingat, dan memecahkan masalah, sehingga prestasinya di sekolah akan menurun.\n" +
                        " \n" +
                        "6. Gangguan Makan dan Tidur\n" +
                        "Anak remaja yang terganggu kesehatan mentalnya dapat mengalami perubahan pola tidur, seperti susah tidur atau sebaliknya tidur berlebihan. Kebiasaan makan pun bisa berubah, seperti kehilangan nafsu makan, atau justru makan berlebihan (stress eating), sehingga membuatnya berpotensi mengalami obesitas.\n" +
                        " \n" +
                        "7. Gangguan Fisik\n" +
                        "Beberapa keluhan fisik yang bisa ditimbulkan oleh masalah kesehatan mental, antara lain sakit kepala, nyeri otot, sakit perut, sakit punggung, tidak bersemangat dan bertenaga.\n" +
                        " \n" +
                        "3.1\tMekanisme Koping Remaja dalam Menghadapi Stres\n" +
                        "Setiap orang memiliki strategi atau mekanisme koping (coping mechanism) saat mengalami stress, keadaan tertekan atau emosi yang negatif. Mekanisme koping ini membantu remaja mengatasi ketidaknyamanan dari berbagai perasaan negatif yang dialaminya, agar keseimbangan emosional tetap terjaga dan remaja dapat belajar beradaptasi dengan setiap perubahan yang dihadapinya.\n" +
                        " \n" +
                        "Mekanisme koping ini ada yang bersifat negatif, seperti makan berlebihan (stress eating), merokok, mengkonsumsi minuman beralkohol dan obat-obatan terlarang atau belanja secara impulsif (impulsive buying). Bagaimana mekanisme koping yang efektif agar remaja dapat mengelola stress dan emosi negatifnya dengan sehat? Berikut cara-caranya:\n" +
                        "1. Mengenali Penyebab Masalah\n" +
                        "Dengan mengetahui penyebab stres atau emosi yang dirasakannya, remaja dapat membuat keputusan dan tindakan yang tepat, seperti mencari bantuan atau konseling, mengakhiri hubungan dengan orang yang menjadi sumber perasaan negatif, atau menetapkan batasan bagi diri sendiri.\n" +
                        " \n" +
                        "2. Berolahraga \n" +
                        "Olahraga seperti bersepeda, jogging, berenang, atau yoga dapat membantu membuat perasaan lebih rileks dan nyaman. \n" +
                        " \n" +
                        "3. Melakukan Hobi  \n" +
                        "Menekuni hobi seperti melukis, menari atau bermain musik juga dapat membantu remaja mengekspresikan diri dan perasaannya. \n" +
                        " \n" +
                        "4. Journaling\n" +
                        "Membuat jurnal tentang pikiran dan perasaan yang dialami, serta mencatat hal-hal yang perlu dilakukan dapat membantu remaja lebih fokus terhadap tindakan yang harus dilakukannya. \n" +
                        " \n" +
                        "5. Self Care\n" +
                        "Merawat diri, beristirahat cukup dan melakukan relaksasi seperti yoga dan menditasi dapat membantu remaja membangun pikiran positif dan kepercayaan diri, serta memaafkan orang atau hal-hal yang menyakitinya.\n" +
                        " \n" +
                        "6. Melakukan Aktivitas yang Disukai\n" +
                        "Bermain dengan hewan peliharaan, traveling ke tempat-tempat baru, masak makanan yang disukai, atau berkebun dapat mengalihkan pikiran dari hal-hal negatif, serta membantu remaja lebih mencintai diri dan hal-hal di sekitarnya. \n" +
                        " \n" +
                        "4.1\tBahaya Self Diagnosis\n" +
                        "Selain mekanisme koping, Anda atau anak remaja Anda juga harus mencari bantuan dari tenaga kesehatan profesional. Namun, seringkali rasa malu dan takut terhadap stigma buruk yang akan diterima dari keluarga dan orang-orang lain, membuat remaja memilih untuk mencari informasi sendiri dan melakukan self diagnosis. Apalagi remaja memiliki akses yang luas terhadap internet dan media sosial, sehingga memungkinkan mereka untuk mendapatkan banyak informasi dari banyak sumber.\n" +
                        " \n" +
                        "Waspada bahaya self diagnosis, karena bisa membuat diagnosa atau analisa yang dilakukan keliru dan tidak tepat. Self diagnosis merupakan tindakan menentukan kondisi kesehatan mental diri sendiri berdasarkan pengalaman pribadi dan informasi yang dicari sendiri, tanpa bantuan tenaga kesehatan profesional. Jika diagnosis yang dilakukan salah, penanganan dan pengobatan gangguan kesehatan mentalnya pun bisa keliru. Hal ini malah akan membuat kondisi kesehatan mental Anda menjadi semakin parah, meningkatkan kecemasan, atau bahkan menimbulkan gangguan kesehatan mental lainnya.\n" +
                        " \n" +
                        "Sebaliknya, jika Anda meminta bantuan tenaga kesehatan profesional, Anda akan mendapatkan diagnosis yang tepat, serta pengelolaan kesehatan mental terbaik bagi kondisi yang sedang Anda alami.\n" +
                        " \n" +
                        "5.1\tPentingnya Teman Bicara\n" +
                        "Selain tenaga kesehatan profesional, Anda pun bisa menceritakan permasalahan yang Anda alami kepada orang-orang yang Anda percaya, seperti orang tua, saudara atau sahabat. Memiliki teman bicara yang baik diyakini dapat meringankan separuh beban yang Anda pikul, karena mereka bisa mendengarkan keluh kesah Anda, menemani dan mendukung di saat sulit, serta memberi perspektif baru pada permasalahan dan pikiran Anda.\n" +
                        " \n" +
                        "Memiliki teman bicara yang baik juga dapat menjauhkan remaja dari risiko depresi dan semakin terpuruk ke dalam pikiran-pikiran negatif, yang dapat berujung pada tindakan menyakiti diri dan bunuh diri.\n" +
                        " \n" +
                        "6.1\tPerundungan di Kalangan Remaja\n" +
                        "Remaja adalah kelompok orang yang rentan mengalami bullying atau perundungan. Perundungan merupakan tindakan mengusik, mengganggu dan menyakiti orang lain yang dilakukan dari waktu ke waktu. Bentuknya bisa berupa:\n" +
                        " \n" +
                        "•\tFisik, seperti memukul, mendorong, menendang, melecehkan, memeras, sampai merusak barang-barang.\n" +
                        "•\tVerbal berupa caci maki, hinaan, atau ejekan.\n" +
                        "•\tDalam hubungan, misalnya dengan menjauhkan pasangan dari keluarga dan teman-teman, mengancam dan menyebarkan kebohongan tentang pasangan, atau melakukan hal-hal yang tidak disukai.\n" +
                        " \n" +
                        "Perundungan berdampak buruk pada kesehatan mental dan membuat korban merasa gelisah, cemas, takut setiap saat, mudah marah dan depresi.\n" +
                        " \n" +
                        "Jika Anda mengalami perundungan, beranikan diri untuk menceritakannya kepada orang-orang yang dipercaya, seperti orang tua, sahabat, guru atau saudara. Anda juga bisa meminta bantuan tenaga ahli, seperti psikolog, atau konseling ke kanal-kanal yang tepat.\n" +
                        " \n" +
                        "7.1\tMeningkatkan Kesadaran Pentingnya Kesehatan Mental\n" +
                        "Orang tua memainkan peranan penting dalam mendukung kesehatan mental anak remaja. Orang tua seharusnya menjadi orang pertama yang sadar, jika anak sedang mengalami gangguan kesehatan mental. \n" +
                        " \n" +
                        "Orang tua harus terlibat dalam kehidupan anak, serta terus menunjukkan cinta, kasih sayang dan perhatian pada mereka. Dorong komunikasi yang terbuka, agar anak berani menceritakan permasalahannya kepada orang tua dan menemukan jalan keluarnya bersama-sama. Beri pujian dan apresiasi atas pencapaian anak agar ia lebih percaya diri. \n" +
                        " \n" +
                        "Selain orang tua dan keluarga, sekolah dan masyarakat juga berperan dalam meningkatkan kesadaran terhadap pentingnya kesehatan mental bagi remaja. Saat ini, ada banyak program dan inisiatif, yang dilakukan oleh sekolah, kampus, pemerintah, maupun institusi global seperti UNICEF yang bisa diikuti oleh remaja dan orang tua untuk mendukung kesejahteraan mental para remaja.\n" +
                        " \n" +
                        "Janganlah segan membicarakan masalah kesehatan mental, serta mencari bantuan jika melihat atau mengalami gangguan kesehatan mental. Keterbukaan merupakan kunci untuk mengatasi masalah kesehatan mental remaja secara lebih cepat.\n",
                R.drawable.img_artikel1
        ));

        ArtikelAdapter adapter = new ArtikelAdapter(getContext(), listArtikel);

        rvArtikel.setLayoutManager(new   LinearLayoutManager(getContext()));
        rvArtikel.setAdapter(adapter);

        return view;
    }
}