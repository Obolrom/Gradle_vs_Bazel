package com.romix.feature.feat48

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat48Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat48UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat48FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat48UserSummary
)

data class Feat48UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat48NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat48Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat48Config = Feat48Config()
) {

    fun loadSnapshot(userId: Long): Feat48NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat48NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat48UserSummary {
        return Feat48UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat48FeedItem> {
        val result = java.util.ArrayList<Feat48FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat48FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat48UiMapper {

    fun mapToUi(model: List<Feat48FeedItem>): Feat48UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat48UiModel(
            header = UiText("Feat48 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat48UiModel =
        Feat48UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat48UiModel =
        Feat48UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat48UiModel =
        Feat48UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat48Service(
    private val repository: Feat48Repository,
    private val uiMapper: Feat48UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat48UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat48UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat48UserItem1(val user: CoreUser, val label: String)
data class Feat48UserItem2(val user: CoreUser, val label: String)
data class Feat48UserItem3(val user: CoreUser, val label: String)
data class Feat48UserItem4(val user: CoreUser, val label: String)
data class Feat48UserItem5(val user: CoreUser, val label: String)
data class Feat48UserItem6(val user: CoreUser, val label: String)
data class Feat48UserItem7(val user: CoreUser, val label: String)
data class Feat48UserItem8(val user: CoreUser, val label: String)
data class Feat48UserItem9(val user: CoreUser, val label: String)
data class Feat48UserItem10(val user: CoreUser, val label: String)

data class Feat48StateBlock1(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock2(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock3(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock4(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock5(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock6(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock7(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock8(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock9(val state: Feat48UiModel, val checksum: Int)
data class Feat48StateBlock10(val state: Feat48UiModel, val checksum: Int)

fun buildFeat48UserItem(user: CoreUser, index: Int): Feat48UserItem1 {
    return Feat48UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat48StateBlock(model: Feat48UiModel): Feat48StateBlock1 {
    return Feat48StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat48UserSummary> {
    val list = java.util.ArrayList<Feat48UserSummary>(users.size)
    for (user in users) {
        list += Feat48UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat48UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat48UiModel {
    val summaries = (0 until count).map {
        Feat48UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat48UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat48UiModel> {
    val models = java.util.ArrayList<Feat48UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat48AnalyticsEvent1(val name: String, val value: String)
data class Feat48AnalyticsEvent2(val name: String, val value: String)
data class Feat48AnalyticsEvent3(val name: String, val value: String)
data class Feat48AnalyticsEvent4(val name: String, val value: String)
data class Feat48AnalyticsEvent5(val name: String, val value: String)
data class Feat48AnalyticsEvent6(val name: String, val value: String)
data class Feat48AnalyticsEvent7(val name: String, val value: String)
data class Feat48AnalyticsEvent8(val name: String, val value: String)
data class Feat48AnalyticsEvent9(val name: String, val value: String)
data class Feat48AnalyticsEvent10(val name: String, val value: String)

fun logFeat48Event1(event: Feat48AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat48Event2(event: Feat48AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat48Event3(event: Feat48AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat48Event4(event: Feat48AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat48Event5(event: Feat48AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat48Event6(event: Feat48AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat48Event7(event: Feat48AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat48Event8(event: Feat48AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat48Event9(event: Feat48AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat48Event10(event: Feat48AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat48Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat48Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat48(u: CoreUser): Feat48Projection1 =
    Feat48Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat48Projection1> {
    val list = java.util.ArrayList<Feat48Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat48(u)
    }
    return list
}
