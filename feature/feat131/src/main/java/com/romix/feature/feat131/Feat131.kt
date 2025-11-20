package com.romix.feature.feat131

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat131Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat131UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat131FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat131UserSummary
)

data class Feat131UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat131NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat131Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat131Config = Feat131Config()
) {

    fun loadSnapshot(userId: Long): Feat131NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat131NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat131UserSummary {
        return Feat131UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat131FeedItem> {
        val result = java.util.ArrayList<Feat131FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat131FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat131UiMapper {

    fun mapToUi(model: List<Feat131FeedItem>): Feat131UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat131UiModel(
            header = UiText("Feat131 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat131UiModel =
        Feat131UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat131UiModel =
        Feat131UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat131UiModel =
        Feat131UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat131Service(
    private val repository: Feat131Repository,
    private val uiMapper: Feat131UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat131UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat131UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat131UserItem1(val user: CoreUser, val label: String)
data class Feat131UserItem2(val user: CoreUser, val label: String)
data class Feat131UserItem3(val user: CoreUser, val label: String)
data class Feat131UserItem4(val user: CoreUser, val label: String)
data class Feat131UserItem5(val user: CoreUser, val label: String)
data class Feat131UserItem6(val user: CoreUser, val label: String)
data class Feat131UserItem7(val user: CoreUser, val label: String)
data class Feat131UserItem8(val user: CoreUser, val label: String)
data class Feat131UserItem9(val user: CoreUser, val label: String)
data class Feat131UserItem10(val user: CoreUser, val label: String)

data class Feat131StateBlock1(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock2(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock3(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock4(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock5(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock6(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock7(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock8(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock9(val state: Feat131UiModel, val checksum: Int)
data class Feat131StateBlock10(val state: Feat131UiModel, val checksum: Int)

fun buildFeat131UserItem(user: CoreUser, index: Int): Feat131UserItem1 {
    return Feat131UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat131StateBlock(model: Feat131UiModel): Feat131StateBlock1 {
    return Feat131StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat131UserSummary> {
    val list = java.util.ArrayList<Feat131UserSummary>(users.size)
    for (user in users) {
        list += Feat131UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat131UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat131UiModel {
    val summaries = (0 until count).map {
        Feat131UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat131UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat131UiModel> {
    val models = java.util.ArrayList<Feat131UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat131AnalyticsEvent1(val name: String, val value: String)
data class Feat131AnalyticsEvent2(val name: String, val value: String)
data class Feat131AnalyticsEvent3(val name: String, val value: String)
data class Feat131AnalyticsEvent4(val name: String, val value: String)
data class Feat131AnalyticsEvent5(val name: String, val value: String)
data class Feat131AnalyticsEvent6(val name: String, val value: String)
data class Feat131AnalyticsEvent7(val name: String, val value: String)
data class Feat131AnalyticsEvent8(val name: String, val value: String)
data class Feat131AnalyticsEvent9(val name: String, val value: String)
data class Feat131AnalyticsEvent10(val name: String, val value: String)

fun logFeat131Event1(event: Feat131AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat131Event2(event: Feat131AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat131Event3(event: Feat131AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat131Event4(event: Feat131AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat131Event5(event: Feat131AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat131Event6(event: Feat131AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat131Event7(event: Feat131AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat131Event8(event: Feat131AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat131Event9(event: Feat131AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat131Event10(event: Feat131AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat131Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat131Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat131(u: CoreUser): Feat131Projection1 =
    Feat131Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat131Projection1> {
    val list = java.util.ArrayList<Feat131Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat131(u)
    }
    return list
}
