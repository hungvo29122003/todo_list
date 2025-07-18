package com.example.todo_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class OnboardingFragment : Fragment() {
    private var imageResId: Int =0
    private var title: String? = null
    private var description: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageResId = it.getInt(ARG_IMAGE_RES_ID)
            title = it.getString(ARG_TITLE)
            description = it.getString(ARG_DESCRIPTION)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding, container, false)
        val imageView = view.findViewById<ImageView>(R.id.onboarding_image)
        val titleTextView = view.findViewById<TextView>(R.id.onboarding_title)
        val descriptionTextView = view.findViewById<TextView>(R.id.onboarding_description)

        imageView.setImageResource(imageResId)
        titleTextView.text = title
        descriptionTextView.text = description

        return view
    }

    companion object {
        private const val ARG_IMAGE_RES_ID = "imageResId"
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"

        @JvmStatic
        fun newInstance(imageResId: Int, title: String, description: String) =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE_RES_ID, imageResId)
                    putString(ARG_TITLE, title)
                    putString(ARG_DESCRIPTION, description)
                }
            }
    }
}