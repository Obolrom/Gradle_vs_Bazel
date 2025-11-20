package com.romix.feature.feat415

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat415Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat415UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat415FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat415UserSummary
)

data class Feat415UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat415NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat415Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat415Config = Feat415Config()
) {

    fun loadSnapshot(userId: Long): Feat415NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat415NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat415UserSummary {
        return Feat415UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat415FeedItem> {
        val result = java.util.ArrayList<Feat415FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat415FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat415UiMapper {

    fun mapToUi(model: List<Feat415FeedItem>): Feat415UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat415UiModel(
            header = UiText("Feat415 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat415UiModel =
        Feat415UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat415UiModel =
        Feat415UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat415UiModel =
        Feat415UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat415Service(
    private val repository: Feat415Repository,
    private val uiMapper: Feat415UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat415UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat415UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat415UserItem1(val user: CoreUser, val label: String)
data class Feat415UserItem2(val user: CoreUser, val label: String)
data class Feat415UserItem3(val user: CoreUser, val label: String)
data class Feat415UserItem4(val user: CoreUser, val label: String)
data class Feat415UserItem5(val user: CoreUser, val label: String)
data class Feat415UserItem6(val user: CoreUser, val label: String)
data class Feat415UserItem7(val user: CoreUser, val label: String)
data class Feat415UserItem8(val user: CoreUser, val label: String)
data class Feat415UserItem9(val user: CoreUser, val label: String)
data class Feat415UserItem10(val user: CoreUser, val label: String)

data class Feat415StateBlock1(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock2(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock3(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock4(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock5(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock6(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock7(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock8(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock9(val state: Feat415UiModel, val checksum: Int)
data class Feat415StateBlock10(val state: Feat415UiModel, val checksum: Int)

fun buildFeat415UserItem(user: CoreUser, index: Int): Feat415UserItem1 {
    return Feat415UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat415StateBlock(model: Feat415UiModel): Feat415StateBlock1 {
    return Feat415StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat415UserSummary> {
    val list = java.util.ArrayList<Feat415UserSummary>(users.size)
    for (user in users) {
        list += Feat415UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat415UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat415UiModel {
    val summaries = (0 until count).map {
        Feat415UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat415UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat415UiModel> {
    val models = java.util.ArrayList<Feat415UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat415AnalyticsEvent1(val name: String, val value: String)
data class Feat415AnalyticsEvent2(val name: String, val value: String)
data class Feat415AnalyticsEvent3(val name: String, val value: String)
data class Feat415AnalyticsEvent4(val name: String, val value: String)
data class Feat415AnalyticsEvent5(val name: String, val value: String)
data class Feat415AnalyticsEvent6(val name: String, val value: String)
data class Feat415AnalyticsEvent7(val name: String, val value: String)
data class Feat415AnalyticsEvent8(val name: String, val value: String)
data class Feat415AnalyticsEvent9(val name: String, val value: String)
data class Feat415AnalyticsEvent10(val name: String, val value: String)

fun logFeat415Event1(event: Feat415AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat415Event2(event: Feat415AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat415Event3(event: Feat415AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat415Event4(event: Feat415AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat415Event5(event: Feat415AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat415Event6(event: Feat415AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat415Event7(event: Feat415AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat415Event8(event: Feat415AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat415Event9(event: Feat415AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat415Event10(event: Feat415AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat415Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat415Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat415(u: CoreUser): Feat415Projection1 =
    Feat415Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat415Projection1> {
    val list = java.util.ArrayList<Feat415Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat415(u)
    }
    return list
}
