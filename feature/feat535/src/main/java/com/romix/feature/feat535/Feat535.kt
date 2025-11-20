package com.romix.feature.feat535

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat535Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat535UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat535FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat535UserSummary
)

data class Feat535UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat535NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat535Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat535Config = Feat535Config()
) {

    fun loadSnapshot(userId: Long): Feat535NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat535NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat535UserSummary {
        return Feat535UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat535FeedItem> {
        val result = java.util.ArrayList<Feat535FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat535FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat535UiMapper {

    fun mapToUi(model: List<Feat535FeedItem>): Feat535UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat535UiModel(
            header = UiText("Feat535 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat535UiModel =
        Feat535UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat535UiModel =
        Feat535UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat535UiModel =
        Feat535UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat535Service(
    private val repository: Feat535Repository,
    private val uiMapper: Feat535UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat535UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat535UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat535UserItem1(val user: CoreUser, val label: String)
data class Feat535UserItem2(val user: CoreUser, val label: String)
data class Feat535UserItem3(val user: CoreUser, val label: String)
data class Feat535UserItem4(val user: CoreUser, val label: String)
data class Feat535UserItem5(val user: CoreUser, val label: String)
data class Feat535UserItem6(val user: CoreUser, val label: String)
data class Feat535UserItem7(val user: CoreUser, val label: String)
data class Feat535UserItem8(val user: CoreUser, val label: String)
data class Feat535UserItem9(val user: CoreUser, val label: String)
data class Feat535UserItem10(val user: CoreUser, val label: String)

data class Feat535StateBlock1(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock2(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock3(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock4(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock5(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock6(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock7(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock8(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock9(val state: Feat535UiModel, val checksum: Int)
data class Feat535StateBlock10(val state: Feat535UiModel, val checksum: Int)

fun buildFeat535UserItem(user: CoreUser, index: Int): Feat535UserItem1 {
    return Feat535UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat535StateBlock(model: Feat535UiModel): Feat535StateBlock1 {
    return Feat535StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat535UserSummary> {
    val list = java.util.ArrayList<Feat535UserSummary>(users.size)
    for (user in users) {
        list += Feat535UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat535UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat535UiModel {
    val summaries = (0 until count).map {
        Feat535UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat535UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat535UiModel> {
    val models = java.util.ArrayList<Feat535UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat535AnalyticsEvent1(val name: String, val value: String)
data class Feat535AnalyticsEvent2(val name: String, val value: String)
data class Feat535AnalyticsEvent3(val name: String, val value: String)
data class Feat535AnalyticsEvent4(val name: String, val value: String)
data class Feat535AnalyticsEvent5(val name: String, val value: String)
data class Feat535AnalyticsEvent6(val name: String, val value: String)
data class Feat535AnalyticsEvent7(val name: String, val value: String)
data class Feat535AnalyticsEvent8(val name: String, val value: String)
data class Feat535AnalyticsEvent9(val name: String, val value: String)
data class Feat535AnalyticsEvent10(val name: String, val value: String)

fun logFeat535Event1(event: Feat535AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat535Event2(event: Feat535AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat535Event3(event: Feat535AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat535Event4(event: Feat535AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat535Event5(event: Feat535AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat535Event6(event: Feat535AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat535Event7(event: Feat535AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat535Event8(event: Feat535AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat535Event9(event: Feat535AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat535Event10(event: Feat535AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat535Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat535Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat535(u: CoreUser): Feat535Projection1 =
    Feat535Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat535Projection1> {
    val list = java.util.ArrayList<Feat535Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat535(u)
    }
    return list
}
