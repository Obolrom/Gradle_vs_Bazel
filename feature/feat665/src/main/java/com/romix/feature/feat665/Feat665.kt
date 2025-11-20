package com.romix.feature.feat665

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat665Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat665UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat665FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat665UserSummary
)

data class Feat665UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat665NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat665Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat665Config = Feat665Config()
) {

    fun loadSnapshot(userId: Long): Feat665NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat665NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat665UserSummary {
        return Feat665UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat665FeedItem> {
        val result = java.util.ArrayList<Feat665FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat665FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat665UiMapper {

    fun mapToUi(model: List<Feat665FeedItem>): Feat665UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat665UiModel(
            header = UiText("Feat665 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat665UiModel =
        Feat665UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat665UiModel =
        Feat665UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat665UiModel =
        Feat665UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat665Service(
    private val repository: Feat665Repository,
    private val uiMapper: Feat665UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat665UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat665UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat665UserItem1(val user: CoreUser, val label: String)
data class Feat665UserItem2(val user: CoreUser, val label: String)
data class Feat665UserItem3(val user: CoreUser, val label: String)
data class Feat665UserItem4(val user: CoreUser, val label: String)
data class Feat665UserItem5(val user: CoreUser, val label: String)
data class Feat665UserItem6(val user: CoreUser, val label: String)
data class Feat665UserItem7(val user: CoreUser, val label: String)
data class Feat665UserItem8(val user: CoreUser, val label: String)
data class Feat665UserItem9(val user: CoreUser, val label: String)
data class Feat665UserItem10(val user: CoreUser, val label: String)

data class Feat665StateBlock1(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock2(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock3(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock4(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock5(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock6(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock7(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock8(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock9(val state: Feat665UiModel, val checksum: Int)
data class Feat665StateBlock10(val state: Feat665UiModel, val checksum: Int)

fun buildFeat665UserItem(user: CoreUser, index: Int): Feat665UserItem1 {
    return Feat665UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat665StateBlock(model: Feat665UiModel): Feat665StateBlock1 {
    return Feat665StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat665UserSummary> {
    val list = java.util.ArrayList<Feat665UserSummary>(users.size)
    for (user in users) {
        list += Feat665UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat665UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat665UiModel {
    val summaries = (0 until count).map {
        Feat665UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat665UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat665UiModel> {
    val models = java.util.ArrayList<Feat665UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat665AnalyticsEvent1(val name: String, val value: String)
data class Feat665AnalyticsEvent2(val name: String, val value: String)
data class Feat665AnalyticsEvent3(val name: String, val value: String)
data class Feat665AnalyticsEvent4(val name: String, val value: String)
data class Feat665AnalyticsEvent5(val name: String, val value: String)
data class Feat665AnalyticsEvent6(val name: String, val value: String)
data class Feat665AnalyticsEvent7(val name: String, val value: String)
data class Feat665AnalyticsEvent8(val name: String, val value: String)
data class Feat665AnalyticsEvent9(val name: String, val value: String)
data class Feat665AnalyticsEvent10(val name: String, val value: String)

fun logFeat665Event1(event: Feat665AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat665Event2(event: Feat665AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat665Event3(event: Feat665AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat665Event4(event: Feat665AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat665Event5(event: Feat665AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat665Event6(event: Feat665AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat665Event7(event: Feat665AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat665Event8(event: Feat665AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat665Event9(event: Feat665AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat665Event10(event: Feat665AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat665Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat665Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat665(u: CoreUser): Feat665Projection1 =
    Feat665Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat665Projection1> {
    val list = java.util.ArrayList<Feat665Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat665(u)
    }
    return list
}
